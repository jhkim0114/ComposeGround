package com.example.composeground.ui.screen

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import java.util.WeakHashMap
import com.example.composeground.databinding.LayoutTermsHostBinding
import com.example.composeground.ui.fragment.TermsFragment
import com.example.composeground.ui.screen.SlideTransitions.backEnter
import com.example.composeground.ui.screen.SlideTransitions.backExit
import com.example.composeground.ui.screen.SlideTransitions.forwardEnter
import com.example.composeground.ui.screen.SlideTransitions.forwardExit
import timber.log.Timber

private object Routes {
    const val Start = "start"
    const val Web = "web"
    const val Native = "native"
}

private object SlideTransitions {
    val forwardEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = SlideDirection.Left,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(200))
    }
    val forwardExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = SlideDirection.Left,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(200))
    }
    val backEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = SlideDirection.Right,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(200))
    }
    val backExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = SlideDirection.Right,
            animationSpec = tween(260, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(200))
    }
}

// ---- [TEST ONLY] In-file FragmentContainer store (Activity-scoped) ----
//  - 키: (FragmentActivity 인스턴스, fragmentTag)
//  - 값: FragmentContainerView (동일 인스턴스 재사용)
//  - WeakHashMap 으로 Activity 종료 시 자동 정리 기대
private object LocalFragmentContainerStore {
    private val store: WeakHashMap<FragmentActivity, MutableMap<String, FragmentContainerView>> = WeakHashMap()

    fun getOrCreate(activity: FragmentActivity, tag: String, create: (Context) -> FragmentContainerView): FragmentContainerView {
        val byTag = store.getOrPut(activity) { mutableMapOf() }
        val existing = byTag[tag]
        if (existing != null) return existing
        val newContainer = create(activity)
        if (newContainer.id == View.NO_ID) newContainer.id = View.generateViewId()
        byTag[tag] = newContainer
        return newContainer
    }

    fun release(activity: FragmentActivity, tag: String) {
        store[activity]?.remove(tag)?.let { container ->
            (container.parent as? ViewGroup)?.removeView(container)
        }
        if (store[activity]?.isEmpty() == true) store.remove(activity)
    }
}

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    var termsContainer by remember { mutableStateOf<FragmentContainerView?>(null) }
    var viewBinding by remember { mutableStateOf<LayoutTermsHostBinding?>(null) }

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Start,
            modifier = Modifier.padding(innerPadding),
            enterTransition = forwardEnter,
            exitTransition = forwardExit,
            popEnterTransition = backEnter,
            popExitTransition = backExit
        ) {
            composable(Routes.Start) {
                Button(
                    onClick = {
                        navController.navigate(Routes.Web)
                    }
                ) { Text("다음") }
            }
            composable(Routes.Web) { backStackEntry ->
//                WebScreen1(
//                    onClick = { navController.navigate(Routes.Native) }
//                )
                WebScreen(
                    backStackEntry = backStackEntry,
                    container = termsContainer,
                    onInitContainer = { termsContainer = it },
                    viewBinding = viewBinding,
                    onInitViewBinding = { viewBinding = it },
                    onClick = { navController.navigate(Routes.Native) }
                )
            }
            composable(Routes.Native) { NativeScreen(
                navController
            ) }
        }
    }
}

@Composable
fun NativeScreen(
    navController: NavHostController
) {
    BackHandler {
        navController.popBackStack()
    }

    Text(text = "native")
}

@Composable
fun WebScreen1(
    onClick: () -> Unit,
) {
    val activity = LocalContext.current as FragmentActivity
    val fm = activity.supportFragmentManager
    val tag = "webview-frag"

    // Keep one container instance across recompositions / back navigation
    val container = remember(activity) {
        FragmentContainerView(activity).apply { id = View.generateViewId() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = {
                // Reattach the same container to the current composition
                (container.parent as? ViewGroup)?.removeView(container)

                // Ensure a single Fragment instance is added once and then only shown/hidden
                val existed = fm.findFragmentByTag(tag) as? TermsFragment
                if (existed == null) {
                    val frag = TermsFragment()
                    fm.commit {
                        setReorderingAllowed(true)
                        add(container.id, frag, tag)
                        show(frag)
                        setMaxLifecycle(frag, androidx.lifecycle.Lifecycle.State.RESUMED)
                    }
                } else {
                    fm.commit {
                        setReorderingAllowed(true)
                        show(existed)
                        setMaxLifecycle(existed, androidx.lifecycle.Lifecycle.State.RESUMED)
                    }
                }

                container
            },
            update = {
                // When recomposed while visible, make sure it's shown & RESUMED
                (fm.findFragmentByTag(tag) as? TermsFragment)?.let { frag ->
                    if (frag.isAdded && frag.isHidden) {
                        fm.commit {
                            setReorderingAllowed(true)
                            show(frag)
                            setMaxLifecycle(frag, androidx.lifecycle.Lifecycle.State.RESUMED)
                        }
                    }
                }
            },
            onRelease = {
                // When this view leaves composition, hide the fragment but KEEP it in back stack
                val frag = fm.findFragmentByTag(tag) as? TermsFragment ?: return@AndroidView
                if (frag.isAdded && !frag.isHidden) {
                    fm.commit {
                        setReorderingAllowed(true)
                        hide(frag)
                        setMaxLifecycle(frag, androidx.lifecycle.Lifecycle.State.STARTED)
                    }
                }
            }
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) { Text("다음") }
    }
}

enum class EnterType { FIRST, REENTER }

@Composable
fun WebScreen(
    container: FragmentContainerView?, // 1번에서 사용
    onInitContainer: (FragmentContainerView) -> Unit, // 1번에서 사용
    viewBinding: LayoutTermsHostBinding?, // 2번에서 사용
    onInitViewBinding: (LayoutTermsHostBinding) -> Unit, // 2번에서 사용
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fragmentTag: String = "terms",
    backStackEntry: NavBackStackEntry,
) {
    val activity = LocalContext.current as FragmentActivity
    val fm = activity.supportFragmentManager

    Box(modifier = modifier.fillMaxSize()) {
        // 1번 FragmentContainerView 방식
        // LayoutTermsHostBinding 제거
/*
        AndroidView(
            factory = { ctx ->
                val activeContainer = container ?: FragmentContainerView(ctx).apply {
                    id = View.generateViewId()
                }
                (activeContainer.parent as? ViewGroup)?.removeView(activeContainer)

                activeContainer.post {
                    val frag = fm.findFragmentByTag(fragmentTag) as? TermsFragment ?: TermsFragment()

                    fm.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(activeContainer.id, frag, fragmentTag)
                        .commitNow()

                    if (container == null) onInitContainer(activeContainer)
                }

                activeContainer
            },
            modifier = Modifier.fillMaxSize()
        )
*/

//        // 2번 viewBinding 방식
//        if (viewBinding != null) {
//            AndroidView(
//                factory = {
//                    (viewBinding.root.parent as? ViewGroup)?.removeView(viewBinding.root)
//                    viewBinding.root.post {
//                        val frag = fm.findFragmentByTag(fragmentTag) as? TermsFragment ?: TermsFragment()
//                        fm.beginTransaction()
//                            .setReorderingAllowed(true)
//                            .replace(viewBinding.termsContainer.id, frag, fragmentTag)
//                            .commitNow()
//                    }
//                    viewBinding.root
//                },
//                modifier = Modifier.fillMaxSize()
//            )
//        } else {
//            AndroidViewBinding(LayoutTermsHostBinding::inflate, modifier = Modifier.fillMaxSize()) {
//                val binding = this
//                val frag = fm.findFragmentByTag(fragmentTag) as? TermsFragment ?: TermsFragment()
//                fm.beginTransaction()
//                    .setReorderingAllowed(true)
//                    .replace(binding.termsContainer.id, frag, fragmentTag)
//                    .commitNow()
//                binding.root.post { onInitViewBinding(binding) }
//            }
//        }


        DisposableEffect(backStackEntry) {
            val obs = LifecycleEventObserver { _, event ->
                Timber.d("@@TEST: ${event.name}")
                Log.d("@@TEST: ","${event.name}")

                if (event == Lifecycle.Event.ON_START) {
                    val entered = backStackEntry.savedStateHandle.get<Boolean>("__entered") == true
                    val type = if (entered) EnterType.REENTER else EnterType.FIRST
                    Log.d("@@TEST: ", "web visible: $type")
                    if (!entered) backStackEntry.savedStateHandle["__entered"] = true
                }
            }
            backStackEntry.lifecycle.addObserver(obs)
            onDispose { backStackEntry.lifecycle.removeObserver(obs) }
        }


        // 3번 store 방식 (테스트용): 파일 내부 Store(LocalFragmentContainerStore)로 컨테이너 보관
        val activeContainer = remember(activity, fragmentTag) {
            LocalFragmentContainerStore.getOrCreate(activity, fragmentTag) { ctx ->
                FragmentContainerView(ctx).apply { id = View.generateViewId() }
            }
        }

        AndroidView(
            factory = {
                // 재부착: 기존 부모에서 떼고 현재 컴포지션에 붙임
                (activeContainer.parent as? ViewGroup)?.removeView(activeContainer)
                // 컨테이너가 윈도우에 attach 된 뒤 프래그먼트 뷰를 현재 컨테이너로 부착
                activeContainer.post {
                    val frag = fm.findFragmentByTag(fragmentTag) as? TermsFragment ?: TermsFragment()
                    fm.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(activeContainer.id, frag, fragmentTag)
                        .commitNow()
                }
                activeContainer
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) { Text("다음") }
    }
}