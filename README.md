# Argon Server

## Goals

* Provide a ready-to-use web application that
  * hosts the oxygen webauthor
  * implements the webauthor integration REST API
  
  ## TODO
 * separate argon-restxq.xqm into an oXygen specific layer and a more general rest layer
 * provide sample basex instance with the project
 * align Java classes (de-duplicate code) with [desktop plugin](https://github.com/axxepta/project-argon), e.g. LockHandler, StreamHandler etc.
 * setup build server and tests


- webapp: A BaseX RestXQ webapp component (folder **webapp**, to be deployed along with a BaseX instance)


### BaseX Webapp
The BaseX webapp mainly consists of a collection of RestXQ endpoints which serve the oXygen plugin
component as REST API.

An additional tree contained in a special view component uses the oXygen JS API to interact
between a database tree and the editor component of the oXygen XML Web Author.

To make sure that the [fancytree](https://github.com/mar10/fancytree) can lazy load by Ajax request from BaseX with 
possibly different host/port (Cross-origin resource sharing),
the Jetty config of the BaseX component has to be adjusted and BaseX version >= 8.6
should be used. \[1\]


\[1\]
[Stackoverflow 42932689](https://stackoverflow.com/questions/42932689/basex-rest-api-set-custom-http-response-header)