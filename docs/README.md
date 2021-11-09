# Home <!-- {docsify-ignore-all} -->

---

**Please note**: Work in progress. All content reflects the current state of discussion, not final decisions.

---

Existing open-source projects address the technical challenges of cataloguing and transferring data 
for a wide range of use cases. However, there is no open-source effort aimed at providing an 
interoperable, cross-organization framework for data sharing, that is built on a common identity 
model and uniform policy enforcement. This project will integrate with existing data exchange 
technologies and provide these missing pieces to create a system for data sharing where each 
organization is able to exert control over how its shared data is used.

A data-sharing system requires a protocol implementation for policy enforcement among participants. 
The Eclipse Dataspace Connector will implement the International Data Spaces (IDS) standard as 
well as relevant protocols associated with the GAIA-X project. With this, the project will provide 
implementation and use case feedback to IDS and GAIA-X. However, the connector will be extensible 
so that it can support various use cases.

## Overview

* Serves as the data exchange endpoint in a federated data system, or *Dataspace*
* Is the foundation of interoperability in *heterogeneous* federated data systems
* Manages data transfer (**sharing**)
  * Data lifecycle
  * Auditing
  * Observability
  * Wire protocols/transfer bindings
* Manages policy control and enforcement (**sovereignty**)
  * Associates policies with data
  * Policy negotiation
  * Policy application

## When to use the Connector?

TBD
 
## Configuration

TBD

## Installation

TBD

## Troubleshooting

TBD

---

## Contributing to the Documentation

These documentation files are rendered by using [Docsify](https://docsify.js.org/). 

Please feel free to add more content to this directory. If you do so, be aware that `.md` files are
automatically rendered without adding any special meta information. You can use basic Markdown. If
you want a file to show up at GitHub pages, either add it to the sidebar, or link it in another 
file.

### How to add new files

Each folder in the `/docs` directory contains files for a section. The `README.md` is the
landing page of this section. Other subsections are placed below and can be linked separately. Feel
free to add sections and subsections, that should be rendered, to the `_sidebar.md`. 

**Note**: Of course, you are free to add documentation that is not rendered at GitHub pages.

### Documenting extensions

As mentioned several times, this project contains multiple extensions and can be extended by 
implementing those. If you add a custom extension, please add at least a `README.md` file to the 
root directory so other users and developers can understand how and where for it is used. For this,
you can and should use the `README.md` file in the `/docs/templates` folder.
