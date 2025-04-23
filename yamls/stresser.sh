#!/bin/bash

URL="http://localhost:8080/events"
CONCURRENT_REQUESTS=50  # Go wild — try 50, 100, or more

echo "🌪️  Launching CPU stress test on $URL with $CONCURRENT_REQUESTS concurrent requests per loop..."
echo "🛑  Press Ctrl+C to stop."

while true; do
  for i in $(seq 1 $CONCURRENT_REQUESTS); do
    curl -s "$URL" > /dev/null &
  done
  wait
done

