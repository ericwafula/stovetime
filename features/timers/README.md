# :features:timers

The Timers tab and the alarm. Every running timer in one list with pause, "+1 min" and cancel, quick timers that aren't part of a lesson, and the full-screen alarm `:app` shows over the lock screen when a timer ends. Screens read and drive timers only through `TimersRepository` in `:core:data`.

### Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:common["common"]
    :core:data["data"]
  end
  subgraph :datasources
    :datasources:local["local"]
    :datasources:network["network"]
  end
  subgraph :features
    :features:timers["timers"]
  end
  subgraph :presentation
    :presentation:ui["ui"]
    :presentation:design["design"]
  end
  subgraph :sdk
    :sdk:timer["timer"]
  end
  :datasources:local --> :core:common
  :core:data --> :core:common
  :core:data --> :sdk:timer
  :core:data --> :datasources:local
  :core:data --> :datasources:network
  :presentation:ui --> :presentation:design
  :presentation:ui --> :core:common
  :features:timers --> :core:data
  :features:timers --> :presentation:ui
  :datasources:network --> :core:common
```