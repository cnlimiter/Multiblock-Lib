![Logo](src/main/resources/assets/multiblocklib/icon.png)

# Multiblock Library

A **WIP** library for the Fabric modloader to help easily create multiblock structures (e.g. machines) in Minecraft.


## Including the Library

```groovy
    repositories {
        maven {
            url 'https://maven.jamalam.tech/releases'
        }
    }

    dependencies {
        modImplementation 'io.github.jamalam360:multiblocklib:{VERSION}'
    }
```

To find the latest version, look at GitHub releases.

## Usage

Multiblocks are registered through a combination of JSON and code.

First, create a JSON file in the `data/[your mod ID]/multiblock_patterns` directory. The name of
the file should be what you want your multiblock to be identified as - you will use it later.

Keys are registered later in code. Each character maps to a `Predicate` you can use to 
match one or many `BlockStates`.

```json
{
  "version": 1,
  "layers": [
    {
      "rows": [
        "GIG",
        "III",
        "GIG"
      ]
    },
    {
      "min": 2,
      "max": 5,
      "rows": [
        "GGG",
        "GIG",
        "GGG"
      ]
    },
    {
      "rows": [
        "GIG",
        "III",
        "GIG"
      ]
    }
  ]
}
```

While the `version` field is not required, it is recommended to explicitly specify the version
to ensure the JSON is parsed correctly regardless of updates to the schema.

**The current latest JSON version is _1_.**

A JSON file such as the one above creates a multiblock like this:

![Picture of test multiblock](/assets/multiblock.png)

The middle layer must occur at least twice, but can occur up to and including five times.

Then, the multiblock must be registered in code using the `MultiblockLib` class, in your `ModInitializer`:

```java
MultiblockLib.INSTANCE.registerMultiblock(
        new Identifier(YOUR_MOD_ID, THE_NAME_OF_YOUR_JSON_FILE),
        TestMultiblock::new,
        MultiblockPatternKeyBuilder.start()
        .where('G', CachedBlockPosition.matchesBlockState(state -> state.getBlock() == Blocks.GLASS))
        .where('I', CachedBlockPosition.matchesBlockState(state -> state.getBlock() == Blocks.IRON_BLOCK))
        .build();
);
```

Next, you must create a class that extends `Multiblock`. This acts as the 'master' of your multiblock
when it is assembled. It receives clicks, and other events, as well as being ticked.

```java
public class TestMultiblock extends Multiblock {
    public TestMultiblock(BlockPos pos, World world, MatchResult match) {
        super(pos, world, match);
    }

    @Override
    public void tick(MultiblockContext context) {
        System.out.println("Tick!");
    }
}
```

You can use your IDE to see what methods are available to you.

You also need an item that assembles/disassembles the multiblock, by using something like this:

```java
@Override
public ActionResult useOnBlock(ItemUsageContext context) {
    Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(context.getWorld(), context.getBlockPos());
    if (multiblock.isPresent()) {
        if (MultiblockLib.INSTANCE.tryDisassembleMultiblock(multiblock.get(), false)) {
            if (context.getWorld().isClient) {
                context.getPlayer().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
            }

            return ActionResult.SUCCESS;
        }
    } else {
        if (MultiblockLib.INSTANCE.tryAssembleMultiblock(context.getWorld(), context.getBlockPos())) {
            if (context.getWorld().isClient) {
                context.getPlayer().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
            }

            return ActionResult.SUCCESS;
        }
    }

    return ActionResult.PASS;
}
```

When you call `MultiblockLib.tryAssembleMultiblock`, it will attempt to assemble a
multiblock. If assembled successfully, it will save the multiblock to the world and instantiate
your `Multiblock` class.

## Notes

- The library has JavaDocs on most of its API classes. Use these for further guidance on usage.
- You shouldn't depend on classes/behaviour inside the `.impl` package. They are subject to change and may
  break in the future. The classes inside `.api` are relatively stable.
