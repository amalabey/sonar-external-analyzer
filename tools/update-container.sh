#!/bin/sh

CONTAINER_NAME=security-integrated-devcontainer_devcontainer-sonarqube-1

echo "Removing previous plugin version"
docker exec $CONTAINER_NAME bash -c "rm -rf /opt/sonarqube/extensions/plugins/sonar-external-analyzer-*"

echo "Copying new plugin version"
docker cp ./target/sonar-external-analyzer-*.jar $CONTAINER_NAME:/opt/sonarqube/extensions/plugins/

echo "Restarting SonarQube docker container"
# docker restart $CONTAINER_NAME
docker logs -f $CONTAINER_NAME