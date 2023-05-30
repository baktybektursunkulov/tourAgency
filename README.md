# tourAgency
## Getting started

#### Portainer docker web UI
Login in docker registry
```
docker login baktybek26
```
Create portainer volume to persist data
```
docker volume create portainer_data
```
Run portainer
```
docker run --restart always --name portainer -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer
```
---

#### docker-compose commands
local-dev
```
docker-compose up -d --build 

```
