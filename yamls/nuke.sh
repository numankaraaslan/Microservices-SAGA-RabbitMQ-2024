#!/bin/bash

set -e

# Step 0: Update Minikube binary from GitHub
echo "⬆️ Checking for Minikube updates..."
LATEST=$(curl -s https://api.github.com/repos/kubernetes/minikube/releases/latest | grep tag_name | cut -d '"' -f 4)
CURRENT=$(minikube version | grep version | awk '{print $3}')

if [[ "$CURRENT" != "$LATEST" ]]; then
  echo "🔄 Updating Minikube from $CURRENT to $LATEST..."
  curl -Lo minikube "https://storage.googleapis.com/minikube/releases/${LATEST}/minikube-linux-amd64"
  chmod +x minikube
  sudo mv minikube /usr/local/bin/minikube
  echo "✅ Minikube updated to $LATEST"
else
  echo "✅ Minikube is already up to date ($CURRENT)"
fi

echo "🔄 Starting full Docker & Minikube reset..."

# Step 1: Fully delete Minikube and cached configs
echo "🧹 Deleting Minikube cluster and purging everything..."
minikube delete --all --purge

# Step 2: Stop all Docker containers
echo "🛑 Stopping all Docker containers..."
docker ps -q | xargs -r docker stop

# Step 3: Remove all Docker containers
echo "🗑 Removing all Docker containers..."
docker ps -aq | xargs -r docker rm -f

# Step 4: Remove all Docker images
echo "🖼 Removing all Docker images..."
docker images -aq | xargs -r docker rmi -f

# Step 5: Remove all Docker volumes
echo "📦 Removing all Docker volumes..."
docker volume ls -q | xargs -r docker volume rm

# Step 6: Remove all custom Docker networks (preserve default: bridge, host, none)
echo "🌐 Removing all custom Docker networks..."
for net in $(docker network ls --format "{{.Name}}" --filter "type=custom"); do
  if [[ "$net" != "bridge" && "$net" != "host" && "$net" != "none" ]]; then
    docker network rm "$net"
  fi
done

# Step 7: Extra Docker system prune for good measure
echo "🧽 Final Docker system prune..."
docker system prune -af --volumes

# Step 8: Confirm nothing remains
echo "✅ Verification..."
echo "Docker Containers:"
docker ps -a
echo "Docker Images:"
docker images
echo "Docker Volumes:"
docker volume ls
echo "Docker Networks:"
docker network ls

# Step 9: Print Summary
echo ""
if [[ -z $(docker ps -aq) && -z $(docker images -aq) && -z $(docker volume ls -q) ]]; then
  echo "🎉 All Docker and Minikube resources fully wiped!"
else
  echo "⚠️ Some Docker resources remain. Inspect manually."
fi

