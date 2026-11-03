# :features:me

The Me tab: total XP, the current and best week streak, this week's cooks against the goal, the cook settings and the history of everything cooked. Progress comes from the rules in `:core:domain`; settings are read and saved through `:core:data`.

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
    :core:domain["domain"]
  end
  subgraph :datasources
    :datasources:local["local"]
    :datasources:network["network"]
  end
  subgraph :features
    :features:me["me"]
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
  :features:me --> :core:data
  :features:me --> :core:domain
  :features:me --> :presentation:ui
  :core:domain --> :core:data
  :datasources:network --> :core:common
```