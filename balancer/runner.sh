sudo docker run -d \
   --name haproxy \
   -v $(pwd):/usr/local/etc/haproxy:ro \
   -p 48080:48080 \
   haproxytech/haproxy-alpine:2.4