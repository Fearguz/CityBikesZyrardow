package pl.ptprogramming.bikeszyrardow

import dagger.Component

@Component(modules = [TestModule::class, TestModuleBinder::class])
interface TestComponent {
    fun inject(test: MainPresenterTest)
}