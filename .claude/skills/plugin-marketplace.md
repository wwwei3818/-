---
name: plugin-marketplace
description: Manage Claude Code plugin marketplace - add marketplaces and install plugins
---

# Plugin Marketplace Manager

Manage Claude Code plugin marketplace: add marketplaces and install plugins.

## Usage

### Add a marketplace

```bash
/plugin marketplace add <owner>/<repo>
```

Example:
```bash
/plugin marketplace add davepoon/buildwithclaude
```

### Install specific plugins

```bash
/plugin install <plugin-name>@<marketplace>
```

Examples:
```bash
/plugin install agents-python-expert@buildwithclaude
/plugin install commands-version-control-git@buildwithclaude
/plugin install hooks-notifications@buildwithclaude
```

### Install all plugins from a marketplace

```bash
/plugin install all-agents@<marketplace>
/plugin install all-commands@<marketplace>
/plugin install all-hooks@<marketplace>
```

Examples:
```bash
/plugin install all-agents@buildwithclaude
/plugin install all-commands@buildwithclaude
/plugin install all-hooks@buildwithclaude
```

## Available Plugin Categories

- **agents** - AI agents with specialized capabilities
- **commands** - Custom slash commands
- **hooks** - Event hooks and notifications

## Quick Setup

To set up the buildwithclaude marketplace with all plugins:

1. Add the marketplace:
   ```bash
   /plugin marketplace add davepoon/buildwithclaude
   ```

2. Install all plugins:
   ```bash
   /plugin install all-agents@buildwithclaude
   /plugin install all-commands@buildwithclaude
   /plugin install all-hooks@buildwithclaude
   ```
