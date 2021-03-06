package code.challenge.moviesInfoApp.listOfMovies.presenter

import android.graphics.Bitmap
import code.challenge.moviesInfoApp.infrastructure.constants.ConstantsListOfMovies.POSTER_PICTURE
import code.challenge.moviesInfoApp.infrastructure.constants.ConstantsListOfMovies.THUMBNAIL_PICTURE
import code.challenge.moviesInfoApp.infrastructure.constants.ConstantsListOfMovies.UNKNOWN
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.presenter.DefaultPresenter
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.views.interfaces.DefaultView
import code.challenge.moviesInfoApp.infrastructure.extensions.buildBitampFromStream
import code.challenge.moviesInfoApp.infrastructure.extensions.buildDrawableAsset
import code.challenge.moviesInfoApp.listOfMovies.model.entities.ListOfMovies
import code.challenge.moviesInfoApp.listOfMovies.model.entities.Movie
import code.challenge.moviesInfoApp.listOfMovies.model.entities.ThumbnailRequest
import code.challenge.moviesInfoApp.listOfMovies.model.repository.ListOfMoviesRepository
import code.challenge.moviesInfoApp.listOfMovies.view.fragments.FragmentMovieList
import code.challenge.moviesInfoApp.listOfMovies.view.fragments.FragmentMoviePoster
import code.challenge.moviesInfoApp.listOfMovies.view.interfaces.ActionMoviePoster
import okhttp3.ResponseBody

class ListOfMoviesPresenter(moviesView: DefaultView): DefaultPresenter(moviesView) {

    private var picturetype = UNKNOWN
    private var thumbnailRequest = ThumbnailRequest()

    private val repository by lazy { ListOfMoviesRepository(this) }
    private val actionPoster by lazy { view as? ActionMoviePoster }

    override fun customSuccesBehavior(result: Any?, request: Any?) {
        result?.let {
            when (it) {
                is ListOfMovies -> repository.updatePage(it)
                is ResponseBody -> pictureResponse(it, request)
            }
        }
    }

    fun movieListSize() = repository.movies.size

    fun getThumbnail(movie: Movie) = repository.getThumbnail(movie)
    fun hasNextPage() = repository.hasNextPage()
    fun nextPage() = repository.nextPage()
    fun updateMovieList() = view.updateListView()
    fun refreshInsertItem(id: Int) = view.updateInsertedList(id)
    //TODO - Criar um view model para abrir um novo fragment
    //TODO -  amarrar o observer no FragmentMovieList
    fun buildPosterThumbnail(movie: Movie) = attachNavigationFragment(FragmentMoviePoster(movie))
    fun buildPosterListOfMovie() = attachNavigationFragment(FragmentMovieList())

    fun loadUpComingMovies(page: Int = 1) = setup().run {
        repository.loadUpComingMovies(page)
    }

    fun takeMove(id: Int) = movieList()
        .takeIf { id in 0 until it.size }?.get(id) ?: Movie()

    fun loadPosterPicture(movie: Movie) = setup().run {
        picturetype = POSTER_PICTURE
        repository.loadPosterPicture(movie)
    }

    fun loadThumbnailPicture(thumbnail: ThumbnailRequest) = setup().run {
        picturetype = THUMBNAIL_PICTURE
        thumbnailRequest = thumbnail
        repository.loadPosterPicture(thumbnail.movie)
    }

    private fun pictureResponse(model: ResponseBody, request: Any? = Any()) {
        when (picturetype) {
            THUMBNAIL_PICTURE -> buildMovieThumbnail(model, request)
            POSTER_PICTURE -> updatePoster(model)
        }
    }

    private fun buildMovieThumbnail(model: ResponseBody, request: Any? = Any()) {
        val thumbnailBitmap = buildBitampFromStream(model.byteStream())
        val thumbKey = request as? String ?: ""
        addMovieThumbnail(thumbKey, thumbnailBitmap)
        refreshMovieList()
    }

    private fun updatePoster(model: ResponseBody) {
        actionPoster?.let {
            val posterBitmap = buildBitampFromStream(model.byteStream())
            it.updatePoster(context.buildDrawableAsset(posterBitmap))
        }
    }

    private fun movieList() = repository.movies
    private fun refreshMovieList() = repository.refreshMovieList(thumbnailRequest)
    private fun addMovieThumbnail(thumbKey: String, thumbnail: Bitmap) =
        repository.addThumbnail(thumbKey, thumbnail)
}