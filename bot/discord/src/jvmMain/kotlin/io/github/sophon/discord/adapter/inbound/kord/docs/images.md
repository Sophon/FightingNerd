# IMAGES

Because of how Discord works, if we want images in the same post as embed:

1. single image - `image` as part of the embed
2. multiple images - a new embed, where we display multiple images
   - **BUT** the `url` of images embed must be identical to the previous embed `url`

Move embeds only display their images once expanded via the Details button, the rules above apply.
Otherwise images are posted by the Media command (Images button).
