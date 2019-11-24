package fr.free.nrw.commons.contributions

import android.database.Cursor
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * The unit test class for ContributionsPresenter
 */
class ContributionsPresenterTest {
    @Mock
    internal var repository: ContributionsRepository? = null
    @Mock
    internal var view: ContributionsContract.View? = null

    private var contributionsPresenter: ContributionsPresenter? = null

    private lateinit var cursor: Cursor

    lateinit var contribution: Contribution

    lateinit var loader: Loader<Cursor>

    /**
     * initial setup
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        cursor = Mockito.mock(Cursor::class.java)
        contribution = Mockito.mock(Contribution::class.java)
        contributionsPresenter = ContributionsPresenter(repository)
        loader = Mockito.mock(CursorLoader::class.java)
        contributionsPresenter?.onAttachView(view)
    }

    /**
     * Test presenter actions onDeleteContribution
     */
    @Test
    fun testDeleteContribution() {
        contributionsPresenter?.deleteUpload(contribution)
        verify(repository)?.deleteContributionFromDB(contribution)
    }


}