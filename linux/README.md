# Initial setting
	
- Prepare USB with startup disk creator
- f9/f12 to boot using usb stick
- if needed, edit grub by pressing the key e
- get list of packages and install: ```wget https://github.com/carlospadron/Notes/blob/master/linux/packages/ubuntu.txt; sudo apt install $(cat paquetes_ubuntu.txt)```
- download and install
	- dropbox ```dropbox start -i```
	- chrome
	- canon mg3550 
	- steam	
	- docker https://docs.docker.com/engine/install/ubuntu/
	- rust
	- vscode
	- github cli
	- pgadmin, https://www.pgadmin.org/download/pgadmin-4-apt/
- connect to accounts via gnome

# AWS
```sudo snap install aws-cli --classic```
```aws configure```

# Coursier
```curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup```

# GIT
git config --global user.name "user"
git config --global user.email "email"
git init <!-- in folder where project should be -->

# GRUB
to make permanent changes to grub
```sudo nano /etc/default/grub```
```sudo update-grub```

# PAM
Read https://www.tecmint.com/configure-pam-in-centos-ubuntu-linux/

# QGIS

Plugins:
- http://mt0.google.com/vt/lyrs=s&hl=en&x={x}&y={y}&z={z}
- Qgis2threejs
- mmqgis
- profile tool
- os translator
- Memory Layer Saver
- qgis2web
- timemanager
- zoom to postcode
- osm place search