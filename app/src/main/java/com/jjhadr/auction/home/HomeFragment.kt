package com.jjhadr.auction.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jjhadr.auction.R
import com.jjhadr.auction.databinding.FragmentHomeBinding
import com.jjhadr.auction.mypage.DBkey.Companion.DB_ARTICLES

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var articleDB: DatabaseReference
    private lateinit var articleAdapter : ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()

    private val listener = object: ChildEventListener {
        // 목록 검색 목록에 대한 추가 수신 대기
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }
        // 목록 변경사항에 대한 대기
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        // 삭제 대기
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        // 항목 순서 변경사항 수신 대기
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private var binding : FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        articleAdapter = ArticleAdapter()
//        articleAdapter.submitList(mutableListOf<ArticleModel>().apply {
//            add(ArticleModel("0","aaaa",10000,"5000",""))
//            add(ArticleModel("0","abb",20000,"10000",""))
//        })
        //프래그먼트는 context가 될수 없기에 getcontext를 가져와야하는데  get  생략가능
        fragmentHomeBinding.articleRecyclerview.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerview.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            //프래그먼트라서 this 안됨
//            if (auth.currentUser != null) {
            val intent = Intent(requireContext(), ArticleAddActivity::class.java)
            startActivity(intent)
//            } else {
               // Snackbar.make(view,"로그인 후 사용해주세요",Snackbar.LENGTH_SHORT).show()
            //}
        }

        articleDB.addChildEventListener(listener)


    }
    //뷰가 다시 보일 떄 데이터 변경
    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()
        articleDB.removeEventListener(listener)
    }

}