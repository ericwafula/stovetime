---
name: add-repository
description: Scaffold a repository in Stovetime's :core:data — a fun-interface per operation plus a parent aggregator, internal Impls that combine the TheMealDB remote and Room local datasources under the injected CachingStrategy, data models, DTO/Cache mappers, and Koin bindings in CoreDataModule.
when_to_use: User asks to "add a repository", expose a datasource to the UI/domain, cache TheMealDB recipes for offline cook mode, read or save progress (XP, streak, completed recipes), or is wiring a new domain end-to-end.
---

# Skill: add-repository

TODO(workshop 1): write this skill. How a Stovetime repository in :core:data reads the phone's copy first, fetches from TheMealDB when it isn't there, saves the result and returns DataResult: one fun interface per operation, an internal Impl, a parent aggregator, the DTO and cache mappers, and its Koin binding.
