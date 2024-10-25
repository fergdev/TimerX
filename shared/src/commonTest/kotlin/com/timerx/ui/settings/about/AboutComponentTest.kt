package com.timerx.ui.settings.about

// class AboutComponentTest : FreeSpec({
//    startKoin {
//        modules(
//            module {
//                single {
//                    mock<Container<AboutMainState, AboutMainIntent, Nothing>> {
//                        every { store } returns mock {
//                            everySuspend { start(any()) } returns Job()
//                        }
//                    }
//                }
//            }
//        )
//    }
//    val defaultAboutComponent = createComponent {
//        DefaultAboutComponent(
//            componentContext = it,
//            back = {}
//        )
//    }
//    "init" {
//        defaultAboutComponent.stack.value.assertActiveInstance<AboutComponent.Child.Main>()
//    }
// })
