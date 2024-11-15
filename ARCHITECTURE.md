# Architecture

## Rendering

Here's how rendering for ships should work:

1. Get a list of blocks to render
2. Convert each into their baked model
3. Translate each of the baked model's meshes based on the required `BlockPos` (relative to the center of the entity)
4. Merge all of the baked models, culling any faces that cannot be seen.
5. Calculate a physics mesh (`AABB`) for the model.
6. Cache this model
7. Render it in the correct position with Flywheel.

## Physics

Here's how collisions will work:

When a player is on a ship, their movement will be overridden with
the movement from a rigid body. This rigid body will be calculated
based on the ship's collider. The movement from this will look and
feel like the player is moving normally, but they will be moving
with the ship.

Once they leave the ship, their movement will be restored.
