package code.challenge.moviesInfoApp.listOfMovies.model.repository.provider

import android.content.Context
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.model.entities.ComunicationProtocolModel
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.model.repository.provider.DefaultProvider
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.network.setCallback
import code.challenge.moviesInfoApp.infrastructure.extensions.defaultCallback
import code.challenge.moviesInfoApp.infrastructure.extensions.loaderHelper

class ListOfMoviesProvider(
    context: Context,
    private val loader: ((ComunicationProtocolModel) -> Any) = loaderHelper
) : DefaultProvider<ListOfMoviesService>(context) {

    override fun loadServiceClass() = ListOfMoviesService::class.java

    fun loadUpComingMovies() {
        service.loadUpComingMoviesService()
            .setCallback(defaultCallback(loader, context))
    }
}