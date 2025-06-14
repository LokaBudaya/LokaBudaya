import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.lokabudaya.data.DataProvider
import com.dev.lokabudaya.data.EventItem
import com.dev.lokabudaya.data.TourItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val firebaseRepository = FirebaseRepository()

    private val _tourItems = MutableStateFlow<List<TourItem>>(emptyList())
    val tourItems = _tourItems.asStateFlow()

    private val _eventItems = MutableStateFlow<List<EventItem>>(emptyList())
    val eventItems = _eventItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                DataProvider.refreshData()
                _tourItems.value = DataProvider.tourItemLists
                _eventItems.value = DataProvider.eventItemLists
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error loading data: ${e.message}")
                // Fallback ke data existing
                _tourItems.value = DataProvider.tourItemLists
                _eventItems.value = DataProvider.eventItemLists
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        loadDataFromFirebase()
    }
}