package com.example.babynote.식단표

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.babynote.Api.INodeJS
import com.example.babynote.Api.RetrofitClient
import com.example.babynote.Common.Common
import com.example.babynote.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_carte_comment_modify.*

class carte_comment_modify : Activity() {
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.7f
        window.attributes = layoutParams
        setContentView(R.layout.activity_carte_comment_modify)

        //Init API
        val retrofit = RetrofitClient.instance
        myAPI = retrofit.create(INodeJS::class.java)

        // 자신이 작성한 댓글 확인하게끔 EditText에 뿌려놓는다.
        var coment = intent.getStringExtra("댓글")
        editText_comment.setText(coment)

        button_delete.setOnClickListener {
            compositeDisposable.add(myAPI.carte_comment_delete(Common.selected_carte_comment?.num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    Toast.makeText(this, "댓글을 삭제하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("carte_comment_delete", message.toString())
                    finish()

                }
                    , { thr ->
                        Toast.makeText(
                            this,
                            "댓글을 삭제하지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        Log.d("carte_comment_delete", thr.message.toString())

                    }

                ))
        }
        button_modify.setOnClickListener {
            if (!editText_comment.text.isEmpty()) {
                compositeDisposable.add(myAPI.carte_modify_comment(
                    Common.selected_carte_comment?.num,
                    Common.selected_carte_comment?.carte_num,
                    Common.selected_carte_comment?.comment_writer,
                    Common.selected_carte_comment?.comment_nickname,
                    editText_comment.text.toString(),
                    Common.selected_carte_comment?.comment_time
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { message ->
                        Toast.makeText(
                            this,
                            "댓글을 수정하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 키보드를 내린다.
                        val imm =
                            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText_comment.windowToken, 0)
                        finish()

                    })
            } else {
                Toast.makeText(this, "댓글을 작성해주세요.", Toast.LENGTH_SHORT).show()
                editText_comment.requestFocus()
                return@setOnClickListener
            }
        }
    }
}
