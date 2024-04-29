## Remote access Ollama service

Running LLM normally consumes much resources, if you want to have another laptop or workstation to serve the Ollama services, you need to enable remote access.

The ollama.service file in this folder enables the remote access by adding environment variable:

> Environment="OLLAMA_HOST=0.0.0.0"

Use the following command to restart the ollama service:

> sudo systemctl daemon-reload
> sudo systemctl restart ollama

Run the following command to open the firewall for it:

> sudo firewall-cmd –permanent –add-port=11434/tcp
> sudo firewall-cmd --reload


## Run InstrcutLab service


