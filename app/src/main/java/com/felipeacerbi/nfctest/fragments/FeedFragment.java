//package com.felipeacerbi.nfctest.fragments;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.felipeacerbi.nfctest.R;
//import com.felipeacerbi.nfctest.adapters.FeedPostsAdapter;
//import com.felipeacerbi.nfctest.models.FeedPost;
//import com.felipeacerbi.nfctest.models.FeedPostMedia;
//import com.felipeacerbi.nfctest.utils.Constants;
//import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
//import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FeedFragment extends Fragment implements View.OnClickListener {
//
//    // Firebase Helper instance
//    private static final int SPAN_COUNT = 2;
//    private FirebaseDBHelper firebaseDBHelper;
//    private FirebaseStoreHelper firebaseStoreHelper;
//    private FloatingActionButton fab;
//    private RecyclerView cardsList;
//    private LayoutManagerType currentLayoutManagerType;
//    private ValueEventListener refreshPosts;
//    private ValueEventListener refreshNewPosts;
//    private FeedPostsAdapter postsAdapter;
//
//    private enum LayoutManagerType {
//        GRID_LAYOUT_MANAGER,
//        LINEAR_LAYOUT_MANAGER
//    }
//
//    public FeedFragment() {
//    }
//
//    public static FeedFragment newInstance(int sectionNumber) {
//        FeedFragment fragment = new FeedFragment();
//        Bundle args = new Bundle();
//        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        firebaseDBHelper = new FirebaseDBHelper(getActivity());
//        firebaseStoreHelper = new FirebaseStoreHelper();
//
//        firebaseDBHelper.getPostsReference()
//                .limitToFirst(10)
//                .addListenerForSingleValueEvent(refreshPosts);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
//
//        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        fab.setOnClickListener(this);
//
//        refreshPosts = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<FeedPost> posts = getPostsFromDB(dataSnapshot);
//                postsAdapter = new FeedPostsAdapter(posts, getActivity());
//                cardsList.setAdapter(postsAdapter);
//
//                firebaseDBHelper.getPostsReference()
//                        .startAt(postsAdapter.getLastUpdateTime().getTimeInMillis())
//                        .addValueEventListener(refreshNewPosts);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        refreshNewPosts = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<FeedPost> posts = getPostsFromDB(dataSnapshot);
//                if(posts.size() > 0 && posts.get(0).getTime().compareTo(postsAdapter.getLastUpdateTime()) == 1) {
//                    for(FeedPost post : posts) {
//                        postsAdapter.addPost(post);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        cardsList = (RecyclerView) rootView.findViewById(R.id.cards_list);
//
//        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//        setRecyclerViewLayoutManager(currentLayoutManagerType);
//
//        return rootView;
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.fab:
//                FeedPostTextDB feedPostTextDB = new FeedPostTextDB(firebaseDBHelper.getLoginName(), Constants.POST_TYPE_TEXT);
//                feedPostTextDB.setContent("Test post!");
//                firebaseDBHelper.getPostsReference().push().setValue(feedPostTextDB);
//                break;
//            default:
//        }
//    }
//
//    public List<FeedPost> getPostsFromDB(DataSnapshot dataSnapshot) {
//        List<FeedPost> feedPosts = new ArrayList<>();
//
//        for(DataSnapshot postReference : dataSnapshot.getChildren()) {
//            int type = (int) postReference.child(Constants.DATABASE_TYPE_CHILD).getValue();
//            if(type == Constants.POST_TYPE_TEXT) {
//                FeedPostTextDB feedPostTextDB = (FeedPostTextDB) postReference.getValue();
//                feedPosts.add(new FeedPostVideo(postReference.getKey(), feedPostTextDB));
//            } else if(type == Constants.POST_TYPE_PHOTO) {
//                FeedPostPhotoDB feedPostPhotoDB = (FeedPostPhotoDB) postReference.getValue();
//                feedPosts.add((new FeedPostMedia(postReference.getKey(), feedPostPhotoDB)));
//            }
//        }
//
//        return feedPosts;
//    }
//
//    public void setRecyclerViewLayoutManager(FeedFragment.LayoutManagerType layoutManagerType) {
//        int scrollPosition = 0;
//
//        if (cardsList.getLayoutManager() != null) {
//            scrollPosition = ((LinearLayoutManager) cardsList.getLayoutManager())
//                    .findFirstCompletelyVisibleItemPosition();
//        }
//
//        RecyclerView.LayoutManager layoutManager;
//        switch (layoutManagerType) {
//            case GRID_LAYOUT_MANAGER:
//                layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
//                currentLayoutManagerType = FeedFragment.LayoutManagerType.GRID_LAYOUT_MANAGER;
//                //if(layoutMenuItem != null) layoutMenuItem.setIcon(R.drawable.ic_view_stream_white_24dp);
//                break;
//            case LINEAR_LAYOUT_MANAGER:
//                layoutManager = new LinearLayoutManager(getActivity());
//                currentLayoutManagerType = FeedFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//                //if(layoutMenuItem != null) layoutMenuItem.setIcon(R.drawable.ic_view_module_white_24dp);
//                break;
//            default:
//                layoutManager = new LinearLayoutManager(getActivity());
//                currentLayoutManagerType = FeedFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
//        }
//
//        cardsList.setLayoutManager(layoutManager);
//        cardsList.scrollToPosition(scrollPosition);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        firebaseDBHelper.getPostsReference()
//                .startAt(postsAdapter.getLastUpdateTime().getTimeInMillis())
//                .removeEventListener(refreshNewPosts);
//    }
//}
