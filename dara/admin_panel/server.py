from flask import Flask, render_template, request, jsonify

app = Flask(__name__)

# In-memory device registry
devices = {}

@app.route("/")
def admin_dashboard():
    return render_template("dashboard.html", devices=devices)

# Called by Android App on launch
@app.route("/api/telemetry", methods=["POST"])
def register_heartbeat():
    data = request.json or {}
    device_id = data.get("device_id")
    if not device_id:
        return jsonify({"status": "error", "message": "Missing device_id"}), 400
    
    devices[device_id] = {
        "device_model": data.get("device_model", "Unknown"),
        "installed_pkg_ver": data.get("installed_pkg_ver", "1.0.0"),
        "last_seen": data.get("timestamp"),
        "pending_update": devices.get(device_id, {}).get("pending_update", None)
    }
    
    pending = devices[device_id]["pending_update"]
    return jsonify({"status": "ok", "suggested_update": pending})

# Admin triggers update for a target device
@app.route("/api/admin/suggest_update", methods=["POST"])
def suggest_update():
    data = request.json or {}
    target_device = data.get("device_id")
    update_pkg = data.get("pkg_name")
    
    if target_device in devices:
        devices[target_device]["pending_update"] = update_pkg
        return jsonify({"success": True, "message": f"Update {update_pkg} pushed to {target_device}"})
    return jsonify({"success": False, "message": "Device not found"}), 404

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
