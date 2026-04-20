#!/usr/bin/env python3
"""
battery_monitor.py -- Real-time battery monitoring
Shows level, temp, voltage, drain rate.
Usage: python3 battery_monitor.py [--interval 5]
"""
import subprocess, time, argparse

def adb(cmd):
    r = subprocess.run(f"adb shell {cmd}", shell=True, capture_output=True, text=True)
    return r.stdout.strip()

def get_battery():
    raw = adb("dumpsys battery")
    info = {}
    for line in raw.splitlines():
        if "level:" in line:
            info["level"] = line.split(":")[-1].strip()
        elif "temperature:" in line:
            info["temp"] = line.split(":")[-1].strip()
        elif "voltage:" in line:
            info["voltage"] = line.split(":")[-1].strip()
        elif "status:" in line:
            info["status"] = line.split(":")[-1].strip()
    return info

parser = argparse.ArgumentParser()
parser.add_argument("--interval", type=int, default=5)
args = parser.parse_args()

try:
    last_level = None
    while True:
        info = get_battery()
        level = int(info.get("level", 0))
        temp = int(info.get("temp", 0)) // 10
        drain = ""
        if last_level and level < last_level:
            drain = f"(-{last_level - level}%)"
        print(f"🔋 {level:3d}% {drain:<8} | {temp}°C | {info.get('status', '?'):<10}")
        last_level = level
        time.sleep(args.interval)
except KeyboardInterrupt:
    print()
