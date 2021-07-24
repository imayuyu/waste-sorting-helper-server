#!/bin/bash
bash ssl_config/config.sh
mvn package || exit 1
docker build --tag charlie0129/waste-sorting-helper-server . || exit 1
# docker push charlie0129/waste-sorting-helper-server || exit 1