package com.example.gkoutsodi.todo_livedata;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TodoAdapter mTodoAdapter;
    private TodoViewModel mTodoViewModel;
    private List<Todo> mTodos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView todoRecyclerView = findViewById(R.id.rvTodos);
        mTodoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        final Observer<List<Todo>> todoObserver = new Observer<List<Todo>>() {
            @Override
            public void onChanged(@Nullable final List<Todo> todos) {
                if (mTodos == null) {
                    mTodos = todos;
                    mTodoAdapter = new TodoAdapter();
                    todoRecyclerView.setAdapter(mTodoAdapter);
                } else {
                    DiffUtil.DiffResult result =
                            DiffUtil.calculateDiff(
                                    new DiffUtil.Callback() {
                                        @Override
                                        public int getOldListSize() {
                                            return mTodos.size();
                                        }

                                        @Override
                                        public int getNewListSize() {
                                            return todos.size();
                                        }

                                        @Override
                                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                                            return mTodos.get(oldItemPosition).getId()
                                                    == todos.get(newItemPosition).getId();

                                        }

                                        @Override
                                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                                            Todo oldTodo = mTodos.get(oldItemPosition);
                                            Todo newTodo = todos.get(newItemPosition);
                                            return oldTodo.equals(newTodo);
                                        }
                                    });

                    result.dispatchUpdatesTo(mTodoAdapter);
                    mTodos = todos;


                }
            }
        };

        mTodoViewModel.getTodos().observe(this, todoObserver);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_todo:
                final EditText nameEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("add new ")
                        .setMessage("whar to tod?")
                        .setView(nameEditText)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = String.valueOf(nameEditText.getText());
                                        long date = (new Date()).getTime();
                                        mTodoViewModel.addTodo(name, date);
                                    }
                                })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

        @Override
        public TodoAdapter.TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
            return new TodoViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TodoAdapter.TodoViewHolder holder, int position) {
            Todo todo = mTodos.get(position);
            holder.getNameTextview().setText(todo.getName());
            holder.getDateTextview().setText(new Date(todo.getDate()).toString());

        }

        @Override
        public int getItemCount() {
            return mTodos.size();
        }


        class TodoViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTvName;
            private final TextView mtTvDate;

            public TodoViewHolder(View itemView) {
                super(itemView);
                mTvName = itemView.findViewById(R.id.tvName);
                mtTvDate = itemView.findViewById(R.id.tvDate);
                Button btnDel = itemView.findViewById(R.id.BtnDelete);
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        Todo todo = mTodos.get(pos);
                        mTodoViewModel.removeTodo(todo.getId() );
                    }
                });
            }

            TextView getNameTextview(){
                return mTvName;
            }

            TextView getDateTextview(){
                return mtTvDate;
            }
        }
    }

}
