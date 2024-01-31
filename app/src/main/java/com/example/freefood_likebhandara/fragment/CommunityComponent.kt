package com.example.freefood_likebhandara.fragment

import com.google.android.datatransport.runtime.dagger.Component

@Component
interface CommunityComponent {
    fun inject(fragment: CommunityFragment)
}