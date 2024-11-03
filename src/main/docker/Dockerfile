# This Dockerfile is used in order to run an nginx container with the requires NGINX configuration.
# Start with the official Nginx image from Docker Hub
FROM nginx:mainline

# Copy the Nginx configuration file to the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Install OpenSSH server and other dependencies
RUN apt-get update && \
    apt-get install -y openssh-server && \
    mkdir /var/run/sshd

# Set the root password for SSH (change "sftp_password" to a secure password)
RUN echo 'root:sftp_password' | chpasswd

# Allow root login via SSH by modifying the SSH configuration
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# Expose Nginx and SSH ports
EXPOSE 80 22

# Start both Nginx and SSH services
CMD ["/bin/bash", "-c", "service ssh start && nginx -g 'daemon off;'"]

