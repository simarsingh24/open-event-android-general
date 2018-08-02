package org.fossasia.openevent.general.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class EditProfileViewModel(private val authService: AuthService, private val authHolder: AuthHolder) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val progress = MutableLiveData<Boolean>()
    val user = MutableLiveData<User>()
    val message = MutableLiveData<String>()

    fun isLoggedIn() = authService.isLoggedIn()

    fun updateUser(firstName: String, lastName: String) {
        val id = authHolder.getId()
        if (firstName.isEmpty() || lastName.isEmpty()) {
            message.value = "Please provide first name and last name!"
            return
        }

        compositeDisposable.add(authService.updateUser(User(id = id, firstName = firstName, lastName = lastName), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    progress.value = true
                }
                .doFinally {
                    progress.value = false
                }
                .subscribe({
                    message.value = "User updated successfully!"
                    Timber.d("User updated")
                }) {
                    message.value = "Error updating user!"
                    Timber.e(it, "Error updating user!")
                })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}