Instructions to set linux (Ubuntu) for personal use.
	
- Prepare USB with startup disk creator
- f9/f12 to boot using usb stick
- if needed, edit grub by pressing the key e
 
			agregar "pci=nommconf" despues de quiet splash, correr con f10 (pasara durante la instalacion y despues)
			cambiar configuracion permanente
				sudo nano /etc/default/grub
				sudo update-grub
		descargar 
			dropbox
			chrome
		dropbox start -i
		sudo apt install $(cat ~/Dropbox/documentos/fichas_tecnicas/paquetes_ubuntu.txt)
		descargar 
			canon mg3550
			skype 
			steam	
			docker https://docs.docker.com/engine/install/ubuntu/
			rust
			vscode
			github cli (see 
		connect to accounts via gnome
		follow qgis intall info
		followg https://www.postgresql.org/ install info
			sudo apt install postgresql postgis
		follow https://www.pgadmin.org/download/pgadmin-4-apt/
			sudo apt install pgadmin4
		github
			ssh-keygen -t ed25519 -C "padron.ca@gmail.com"
			cat ~/.ssh/id_ed25519.pub
		aws cli
			sudo snap install aws-cli --classic
			aws configure
		coursier
			curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
       
	QGIS plugins and layers
		http://mt0.google.com/vt/lyrs=s&hl=en&x={x}&y={y}&z={z}
		Qgis2threejs
		mmqgis
		profile tool
		os translator
		Memory Layer Saver
		qgis2web
		timemanager
		zoom to postcode
		osm place search
	CONFIGURAR
		Git
			git config --global user.name "user"
			git config --global user.email "email"
			git init # in folder where project should be		
		Seguriad
			pam
