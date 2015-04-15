# kxssh
Java SSH client (wrapper for jsch)

master: [![Build Status](https://travis-ci.org/kemitix/kxssh.svg?branch=master)](https://travis-ci.org/kemitix/kxssh)
develop: [![Build Status](https://travis-ci.org/kemitix/kxssh.svg?branch=develop)](https://travis-ci.org/kemitix/kxssh)

## Usage

### Password Authenticated Client

    SftpClient client
            = KxSsh.getSftpClient(hostname, username, password);

### Private Key Authenticated Client

    String privatekey = "~/.ssh/id_rsa";
    SftpClient client
            = KxSsh.getSftpClient(hostname, username, privatekey, passphrase);

### Download a file

Download a file from a remote host and save it locally:

    client.download(remoteFilename, new File(localFile));

### Upload a file

Upload a local file to a remote host:

    client.upload(new File(localFile), remoteFilename);

## TODO

* Better readme and javadoc
