# IMAGES

Because of how Discord works, if we want images in the same post as embed:

1. single image - `image` as part of the embed
2. multiple images - a new embed, where we display multiple images
   - **BUT** the `url` of images embed must be identical to the previous embed `url`

Games that are **not** `isCollapsedByDefault` always display their images, the rules above apply.
