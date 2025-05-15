package upb.test.icuapharmacy;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.products = products;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewProductName);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
            productImage = itemView.findViewById(R.id.imageViewProduct);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
