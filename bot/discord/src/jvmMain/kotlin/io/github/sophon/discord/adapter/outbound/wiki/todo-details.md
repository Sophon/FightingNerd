# TODO: Details button

## Overview
Move embeds have a Details button that expands the embed in place.
Currently, the message ID and the expanded embed are kept in an in-memory map and evicted after ~30s.
This scales badly, and the map is lost on restart/redeploy, so pending buttons die.

Goal: make the button stateless. It carries the request in its `custom_id`; on click, we run the normal request pipeline with an internal flag for the expanded form.

## Points
- One request pipeline, one usecase, one embed builder
- The request model has an `expanded` flag (or `DetailLevel`), defaulting to collapsed
- Slash and tag parsers never set it - not a command, option or keyword; nothing for the user to type
- Only the button handler sets it - parses `custom_id` into the same request, sets `expanded = true`, runs the pipeline, edits the message
