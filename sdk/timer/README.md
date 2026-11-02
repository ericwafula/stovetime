# :sdk:timer

The cook-timer engine: any number of named timers, kept alive by one foreground service with one ongoing notification, and rung on time by exact alarms even when the app is closed or the phone is locked. It has no Koin, Timber or project dependencies; `:app` configures it.

## Setup

Call `initialize` once from `Application.onCreate`, before anything else touches timers. The alarm, notification-action and boot receivers rely on it having run when the system starts the process for them.

```kotlin
StovetimeTimers.initialize(
    context = this,
    config =
        TimerConfig(
            smallIcon = R.drawable.ic_timer,
            openAppIntent = { payload -> Intent(this, MainActivity::class.java).putExtra("cook_step", payload) },
            alarmActivity = AlarmActivity::class.java,
        ),
)
```

After that, `StovetimeTimers.engine` (or the returned `TimerEngine`) is the whole contract: `timers` as a `StateFlow` (soonest-ending first, paused after running), `start`, `pause`, `resume`, `addMinute`, `cancel`, `stopRinging` (Stop) and `snooze` (+1 min). `alarmActivity` receives the timer id in `StovetimeTimers.EXTRA_TIMER_ID`.

The app asks for `POST_NOTIFICATIONS` and, when `exactAlarmsAllowed` is false, explains that alarms may be a little late.

### Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR

```