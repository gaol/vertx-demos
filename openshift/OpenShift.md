# How to run OpenShift locally

* Install crc following instruction from https://developers.redhat.com/products/openshift-local/overview

* Set up http(s) proxy in case you need to check out codes from github in constraint area, like in China.

> crc config set http-proxy  <your-http-prxy-url>
> 
> crc config set https-proxy <your-https-proxy-url>

* Run `crc setup`, it will load and cache bits into `~/.crc/cache/`

Some output in mine is like:
```shell
INFO Checking if libvirt 'crc' network is available 
INFO Checking if libvirt 'crc' network is active  
INFO Checking if CRC bundle is extracted in '$HOME/.crc' 
INFO Checking if /home/lgao/.crc/cache/crc_libvirt_4.11.7_amd64.crcbundle exists 
Your system is correctly setup for using CRC. Use 'crc start' to start the instance
```


* Then you can start the Openshift cluster by: `crc start`, the output is like:

```shell
...
INFO Adding crc-admin and crc-developer contexts to kubeconfig... 
Started the OpenShift cluster.

The server is accessible via web console at:
  https://console-openshift-console.apps-crc.testing

Log in as administrator:
  Username: kubeadmin
  Password: 7K6Ba-w4CPK-ciiLP-TfgP7

Log in as user:
  Username: developer
  Password: developer

Use the 'oc' command line interface:
  $ eval $(crc oc-env)
  $ oc login -u developer https://api.crc.testing:6443

```

* Run the commands following output above:

> `eval $(crc oc-env)`
> 
> `oc login -u developer https://api.crc.testing:6443`

* Then you can start your demo with creating a new projct:

> oc new-project <project-name>
