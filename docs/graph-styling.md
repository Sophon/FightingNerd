# GRAPH AND DIAGRAM STYLING

Mermaid.js conventions for architecture/flow diagrams.

Inspired by Ashley Peacock's Creating Software with Modern Diagramming Techniques. 

UML and C4.

## Colors
- layers
  - orange - inbound ports
  - green - application
  - blue - adapters
  - no color - feature modules
- dashed - port interfaces
- purple - class functions

## Rules
- nodes: stroke only, no fill
- no-arrow line = static wiring (port ↔ adapter ↔ feature)
