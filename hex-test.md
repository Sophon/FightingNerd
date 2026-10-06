
Change the hex architecture test:

- everything is internal or private, with the exceptions of:
  - root model
  - inPort
  - files at the root level (like FeatureInfo and DI module)
- at the root level, only these packages are allowed
  - adapter
    - inbound
    - outbound
  - app
    - model
    - outPort
    - service
    - util
  - docs
  - inPort
  - model
- everything inside inPort must be interface
- service:
  - everything inside must have suffix `Service`
  - not sure if possible, but all services must be implementing something
    - internal services implement the interfaces "of themselves"
    - other interfaces implement the in ports
  - a service can only call another service, an outbound port or a util
- for phase 1, we want to exclude the composeApp module
  - do it via annotation or via some rule, doesn't matter to me

Anything else?