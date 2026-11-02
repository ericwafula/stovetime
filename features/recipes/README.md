# :features:recipes

The Recipes tab: browse and search the catalogue synced from TheMealDB, and the recipe page with its ingredients. Screens read only from `:core:data`, which keeps the phone's copy fresh in the background.

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
    :features:recipes["recipes"]
  end
  subgraph :presentation
    :presentation:ui["ui"]
    :presentation:design["design"]
  end
  :datasources:local --> :core:common
  :features:recipes --> :core:data
  :features:recipes --> :presentation:ui
  :core:data --> :core:common
  :core:data --> :datasources:local
  :core:data --> :datasources:network
  :presentation:ui --> :presentation:design
  :presentation:ui --> :core:common
  :datasources:network --> :core:common
```