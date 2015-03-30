# kxssh
Java SSH client (wrapper for jsch)

master: [![Build Status](https://travis-ci.org/kemitix/kxssh.svg?branch=master)](https://travis-ci.org/kemitix/kxssh)
develop: [![Build Status](https://travis-ci.org/kemitix/kxssh.svg?branch=develop)](https://travis-ci.org/kemitix/kxssh)

## Usage

### Download a file

Download a file from a remote host and save it locally:

    SftpClient client
            = SshClient.getSftpClient(hostname, username, password);
    client.download(remoteFilename, new File(localFile));

## TODO

* Upload a file
* Private key authentication
* Better readme and javadoc
