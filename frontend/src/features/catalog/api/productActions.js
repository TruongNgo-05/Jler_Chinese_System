import { favoritesApi } from "@/features/favorites/api/favoritesApi";
import { cartApi } from "@/features/cart/api/cartApi";

export function saveSelection(favorite, body) {
  return favorite ? favoritesApi.toggle(body) : cartApi.add(body);
}
