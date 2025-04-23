#!/bin/bash

# 🌀 Run forever until Ctrl+C
while true; do
  echo "🌪 Chaos round at $(date)"

  # 🧾 Get all pods (exclude those in Completed or Error state)
  pods=($(kubectl get pods --no-headers | grep -vE 'Completed|Error' | awk '{print $1}'))

  # 🔁 Randomly shuffle and select 1 or 2 pods
  count=$((RANDOM % 2 + 1))  # 1 or 2
  victims=($(shuf -e "${pods[@]}" -n $count))

  for pod in "${victims[@]}"; do
    echo "🔥 Deleting pod: $pod"
    kubectl delete pod "$pod" --grace-period=0 --force
  done

  echo "😴 Sleeping 3 minutes..."
  sleep 180
done

