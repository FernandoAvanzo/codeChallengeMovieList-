package code.challenge.moviesInfoApp.listOfMovies.model.repository

import android.graphics.Bitmap
import code.challenge.moviesInfoApp.infrastructure.defaultComponents.model.entities.AuthApiModel
import code.challenge.moviesInfoApp.infrastructure.extensions.buildMoviePictureUrl
import code.challenge.moviesInfoApp.listOfMovies.model.entities.ListOfMovies
import code.challenge.moviesInfoApp.listOfMovies.model.entities.Movie
import code.challenge.moviesInfoApp.listOfMovies.model.entities.ThumbnailRequest
import code.challenge.moviesInfoApp.listOfMovies.model.repository.provider.ListOfMoviesProvider
import code.challenge.moviesInfoApp.listOfMovies.presenter.ListOfMoviesPresenter

class ListOfMoviesRepository(private val presenter: ListOfMoviesPresenter) {

    private var currentPage = ListOfMovies()

    val movies by lazy { HashMap<Int, Movie>() }

    private val keyMovies by lazy { HashMap<Movie, Int>() }

    private val posterAuth by lazy { AuthApiModel(buildMoviePictureUrl(), "") }

    private val provider by lazy {
        ListOfMoviesProvider(
            presenter.view.viewContext(),
            presenter::defaultLoader
        )
    }

    private val posterProvider by lazy {
        ListOfMoviesProvider(
            presenter.view.viewContext(),
            presenter::defaultLoader,
            posterAuth
        )
    }

    fun loadUpComingMovies(page: Int = 1) = provider.loadUpComingMovies(page)
    fun addThumbnail(thumbKey: String, thumbnail: Bitmap) = moviesThumbnails.put(thumbKey, thumbnail)
    fun getThumbnail(movie: Movie) = moviesThumbnails[movie.posterPath]
    fun hasNextPage() = currentPage.page < currentPage.totalPages
    fun nextPage() = currentPage.page + 1

    fun refreshMovieList(thumbnail: ThumbnailRequest) =
        presenter.refreshInsertItem(thumbnail.movieId)

    fun loadPosterPicture(movie: Movie?){
        movie?.let {
            posterProvider.loadPosterPicture(it.posterPath?:"")
        }
    }

    fun updatePage(page: ListOfMovies) {
        currentPage = page
        var index = movies.size
        page.results.indices.map {
            index = updateMovies(page.results[it], index)
        }
        presenter.updateMovieList()
    }

    private fun updateMovies(movie: Movie, index: Int): Int {
        return takeIf { keyMovies.containsKey(movie).not() }?.let {
            updateThumbnail(ThumbnailRequest(index, movie))
            movies[index] = movie
            keyMovies[movie] = index
            index + 1
        } ?: index
    }

    private fun updateThumbnail(thumbnail: ThumbnailRequest){
        val movie = thumbnail.movie
        takeIf { moviesThumbnails.containsKey(movie.posterPath).not() }?.let {
            presenter.loadThumbnailPicture(thumbnail)
        }
    }

    companion object {
        private val moviesThumbnails by lazy { HashMap<String, Bitmap>() }
    }
}