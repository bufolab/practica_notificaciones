package com.test.appnotificaciones

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokensListView.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val mytoken = task.result?.token

                pushTokeDatabase(mytoken)

                //actualizamos el texto en pantalla
                token.text = mytoken

                listenTokenDatabase()
            })
    }

    private fun listenTokenDatabase() {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("tokens")
        myRef.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.i(TAG,"onCancelled")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.i(TAG,"onChildMoved")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.i(TAG,"onChildRemoved")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.i(TAG,"onChildChanged")

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.i(TAG,"onChildAdded")
                var adapter = tokensListView.adapter as ArrayAdapter<String>

                val token = p0.getValue(TokenDatabase::class.java)
                adapter.add(token?.token)
            }
        })
    }

    private fun pushTokeDatabase(mytoken: String?) {
        //recogemos referencia a base de datos
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("tokens")
        //actualizamos base de datos
        val parameters = hashMapOf<String, String?>()
        parameters["token"] = mytoken
        myRef.push().updateChildren(parameters.toMap())
    }
}

class TokenDatabase{
    val token:String?=null
}

