package com.example.core.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel<VE: ViewEvents,S: ViewState>(initialState:S):ViewModel(){

    private val disposables= CompositeDisposable()


    protected val _viewEvents=SingeLiveEvent<VE>()
    val viewEvents: LiveData<VE> = _viewEvents

    private val _viewState= MutableLiveData<S>(initialState)
    val viewState:LiveData<S> = _viewState

    private var lastState=initialState

    protected fun setState(reducer:S.() -> S ){
        val newState = reducer(lastState)
        lastState=newState
        _viewState.postValue(newState)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun Disposable.disposeOnClear():Disposable{
        disposables.add(this)
        return this
    }

    fun <T> Observable<T>.execute(
        stateReducer: S.(Async<T>) -> S
    )= execute({ it },null,stateReducer)

    fun <T, V> Observable<T>.execute(
        mapper:(T)-> V,
        successMetaData: ((T) -> Any)?=null,
        stateReducer: S.(Async<V>) -> S
    ):Disposable{
        setState { stateReducer(Loading()) }

        return map<Async<V>> {value->
            val success=Success(mapper(value))
            success.metadata=successMetaData?.invoke(value)
            success
        }
            .onErrorReturn { e ->
                println("Observerable encountered error")
                Fail(e)
            }
            .subscribe{ asyncData -> setState { stateReducer(asyncData) }}
            .disposeOnClear()
    }

}