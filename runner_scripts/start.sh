docker run \
    -p 10883:10883 \
    -p 3306:3306 \
    --add-host host.docker.internal:host-gateway \
    charlie0129/waste-sorting-helper-server
