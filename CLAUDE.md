# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Impaled is a Fabric mod for Minecraft 1.19.3 that adds trident variants and the Sincere Loyalty enchantment. The project combines two main components:
- **Impaled**: Core mod adding new trident variants (Pitchfork, Hellfork, Soulfork, Elder Trident, Atlan)
- **SincereLoyalty**: Adds Loyalty IV enchantment allowing manual trident recall

## Build Commands

- `./gradlew build` - Build the mod
- `./gradlew runClient` - Run Minecraft client with the mod loaded for testing
- `./gradlew runServer` - Run Minecraft server with the mod loaded for testing
- `./gradlew clean` - Clean build artifacts
- `./gradlew publishToMavenLocal` - Publish to local Maven repository

## Architecture

### Entry Points
- `ladysnake.impaled.common.Impaled` - Main mod initialization
- `ladysnake.sincereloyalty.SincereLoyalty` - Sincere Loyalty component initialization
- `ladysnake.impaled.client.ImpaledClient` - Client-side initialization
- `ladysnake.sincereloyalty.SincereLoyaltyClient` - Client-side Sincere Loyalty initialization

### Key Components

**Core Systems:**
- `ladysnake.impaled.common.init.ImpaledItems` - Item registration and initialization
- `ladysnake.impaled.common.init.ImpaledEntityTypes` - Entity type registration
- `ladysnake.sincereloyalty.storage.LoyalTridentStorage` - Manages persistent trident recall data

**Trident Items:**
- `ImpaledTridentItem` - Base class for all custom trident variants
- `PitchforkItem` - Early game trident with farmland tilling ability
- `HellforkItem` - Nether variant with fire-based mechanics
- `ElderTridentItem` - Advanced trident with homing capabilities
- `AtlanItem` - Legendary golden trident accepting sword enchantments
- `MaelstromItem` - Creative-only item for launching tridents from inventory

**Mixins:**
The mod uses extensive Mixin integration in two packages:
- `ladysnake.impaled.mixin.*` - Core trident functionality modifications
- `ladysnake.sincereloyalty.mixin.*` - Loyalty enchantment modifications

### Development Notes

- Minecraft version: 1.19.3
- Java version: 17
- Uses Fabric Loom build system
- Mixins require `compatibilityLevel: "JAVA_17"`
- Two separate mixin configurations: `impaled.mixins.json` and `sincereloyalty.mixins.json`
- Mod publishes to CurseForge, Modrinth, and GitHub via Chenille plugin

### Testing

The mod includes client and server run configurations. Test new trident variants by:
1. Running `./gradlew runClient`
2. Creating a new world or using creative mode
3. Accessing items through creative inventory or crafting recipes in `src/main/resources/data/impaled/recipes/`