# Terminology

| Name                                   | Description                                         |
|:---                                    |:---                                                 |
| **Artifact**                           |
| **Asset**                              | - has one content type<br/>- can be a finite as a (set of) file(s) or non finite as a service, stream</br>- is the _unit of sharing_<br/>- can point to one or more (physical) asset elements
| **Asset Element**                      |
| **Asset Index**                        | - manages assets<br/>- is provided by an extension<br/>- may support external catalogs<br/>- can be queried 
| **Broker**                             | see **IDS Broker**
| **Connector**                          |
| **Connector Directory**                |
| **Contract**                           |
| **Contract Agreement**                 | - points to a **Contract Offer**<br/>- results from a **Contract Negotiation Process**<br/>- has a start date and may have a expiry date and a cancellation date
| **Contract Negotiation**               | - MVP: only possible to accept already offered contracts. Counter offers are rejected automatically.
| **Contract Offer**                     | - is a set of obligations and permissions<br/>- is generated on the fly on provider side (see **Contract Offer Framework**)<br/>- is immutable<br/>- is persisted within the **Contract Negotiation Process** once the negotiation has started<br/>
| **Contract Offer Framework**           | - generates **Contract Offer Templates**<br/>- is provided by **Extensions**<br/>- may be implemented in custom extensions to created contract offers based on existing systems
| **Contract Offer Template**            | - is a blueprint of a **Contract Offer**
| **Consumer**                           |
| **Data**                               |
| **Dataspace**                          |
| **EDC Extension**                      |
| **Element**                            | see **Asset Element**
| **Extension**                          | see **EDC Extension**
| **Framework**                          | see **Contract Offer Framework**
| **Identity Provider**                  |
| **IDS Broker**                         | - is the IDS version of the **Connector Directory**
| **Offer**                              | see **Contract Offer**
| **Offer Framework**                    | see **Contract Offer Framework**
| **Policy**                             | - is a logical collection of rules
| **Provider**                           |
| **Resource**                           |
| **Resource Manifest**                  |
| **Rule**                               | - is bound to a **Contract Offer**, **Contract Agreement**, or **Contract Offer Framework**<br/>- exists independent from an **Asset**
| **Transfer Process**                   | - is based on a **Contract Agreement**
