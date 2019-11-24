package fr.free.nrw.commons.contributions;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class ContributionsViewModel extends ViewModel {

    private ContributionsRepository repository;
    private MutableLiveData<Integer> currentItemPositionLiveData =new MutableLiveData<>();

    //TODO handle this the dagger way
    void setRepository(ContributionsRepository contributionsRepository){
        this.repository=contributionsRepository;
        currentItemPositionLiveData.postValue(-1);
    }

    public LiveData<List<Contribution>> getAllContributions(){
        return this.repository.getAll();
    }


    public void deleteUpload(Contribution contribution) {
        repository.deleteContributionFromDB(contribution);
    }

    public void setCurrentItemPosition(int currentPageNumber){
        currentItemPositionLiveData.postValue(currentPageNumber);
    }

    public MutableLiveData<Integer> getCurrentItemPositionLiveData() {
        return currentItemPositionLiveData;
    }
}
