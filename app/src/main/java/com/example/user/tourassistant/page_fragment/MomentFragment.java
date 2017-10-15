package com.example.user.tourassistant.page_fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.user.tourassistant.R;
import com.example.user.tourassistant.firebase.Moment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MomentFragment extends Fragment {






    private RecyclerView momentListView;
    private Toolbar toolbar;
    private DatabaseReference mDatabase,userDatabase;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Moment,MomentViewHolder> firebaseRecyclerAdapter;






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    public MomentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        mDatabase = FirebaseDatabase.getInstance().getReference().child("moments");
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        momentListView = (RecyclerView)getActivity().findViewById(R.id.recyclerview_moment);
        momentListView.setHasFixedSize(true);
        momentListView.setLayoutManager(llm);

        FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm3 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft3 = fm3.beginTransaction();
                AddMomentFragment addMomentFragment = new AddMomentFragment();
                ft3.replace(R.id.homeFragmentView,addMomentFragment);
                ft3.addToBackStack(null);
                ft3.commit();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moment,MomentViewHolder>(
                Moment.class, R.layout.row_blog, MomentViewHolder.class, mDatabase
        ) {
            @Override
            protected void populateViewHolder(MomentViewHolder viewHolder, Moment model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity(),model.getImage());
                viewHolder.setUserName(model.getUserName());
            }
        };

        momentListView.setAdapter(firebaseRecyclerAdapter);

    }


    public static class MomentViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MomentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView viewTitle = (TextView)mView.findViewById(R.id.tv_view_title);
            viewTitle.setText(title);
        }

        public void setDesc(String desc){
            TextView viewDesc = (TextView)mView.findViewById(R.id.tv_view_description);
            viewDesc.setText(desc);
        }

        public void setImage(Context imgContext, String image){

            final ProgressBar progressBar = new ProgressBar(imgContext);
            ImageView viewImage = (ImageView)mView.findViewById(R.id.iv_view_image);

            //Picasso.with(imgContext).load(image).into(viewImage);
            //Glide.with(imgContext).load(image).into(viewImage);
            int radius = 30; // corner radius, higher value = more rounded
            int margin = 10; // crop margin, set to 0 for corners with no crop
           /* Glide.with(imgContext)
                    .load(image)
                    .bitmapTransform(new RoundedCornersTransformation(imgContext, radius, margin))
                    .into(viewImage);*/

            progressBar.setVisibility(View.VISIBLE);

            Glide.with(imgContext)
                    .load(image)
                    .bitmapTransform(new RoundedCornersTransformation(imgContext, radius, margin))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // resizes the image to 100x200 pixels but does not respect aspect ratio
                    .fitCenter() // scale to fit entire image within ImageView
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewImage);

        }

        public void setUserName(String userName){
            TextView viewUserName = (TextView)mView.findViewById(R.id.tv_view_posterName);
            viewUserName.setText(userName);
        }
    }




}