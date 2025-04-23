#!/bin/bash
# docker compose build first

minikube start --driver=docker --profile=minikube --cpus=8 --memory=10000

# List of image names to load into Minikube
IMAGES=(
  ab-eureka:latest
  ab-config-server:latest
  ab-gateway:latest
  ab-userservice:latest
  ab-userservice-failover:latest
  ab-activityservice:latest
  ab-activityservice-failover:latest
  ab-paymentservice:latest
  ab-paymentservice-failover:latest
  ab-mailservice:latest
  ab-website:latest
  ab-util:latest
)

echo "📦 Loading images into Minikube..."

for image in "${IMAGES[@]}"; do
  echo "➡️  Loading $image ..."
  minikube image load "$image"
done

echo "✅ All images loaded successfully into Minikube!"

set -e

# STEP 1: Apply all manifests
echo "🔧 Applying all Kubernetes YAML files..."
kubectl apply -f postgresql.yaml \
              -f rabbitmq.yaml \
              -f eureka.yaml \
              -f config-server.yaml \
              -f gateway.yaml \
              -f mailservice.yaml \
              -f website.yaml \
              -f activityservice.yaml \
              -f activityservice-hpa.yaml \
              -f userservice.yaml \
              -f paymentservice.yaml \
              -f userservice-failover.yaml \
              -f activityservice-failover.yaml \
              -f paymentservice-failover.yaml

# Helper function: wait for pod by app label
wait_for_pod_ready() {
  local label=$1
  echo "⏳ Waiting for pod with label $label to be running..."
  while true; do
    STATUS=$(kubectl get pods -l app=$label -o jsonpath="{.items[0].status.phase}" 2>/dev/null || echo "Pending")
    if [ "$STATUS" == "Running" ]; then
      echo "✅ Pod for $label is running."
      break
    fi
    sleep 2
  done
}

# STEP 2: Wait for key services to be ready
wait_for_pod_ready eureka
wait_for_pod_ready rabbitmq
wait_for_pod_ready gateway
wait_for_pod_ready website

# STEP 3: Start port-forwards (in background)
echo "🚪 Starting port-forwards..."
kubectl port-forward service/eureka 8761:8761 &
kubectl port-forward service/rabbitmq 15672:15672 &
kubectl port-forward service/gateway 4441:4441 &
kubectl port-forward service/website 8080:8080 &

echo "✅ All services port-forwarded."

