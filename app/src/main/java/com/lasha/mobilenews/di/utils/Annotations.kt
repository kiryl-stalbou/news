package com.lasha.mobilenews.di.utils

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ArticlesReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoriesReference

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SubCategoriesReference