import { lazy } from "react";
import { RoutePage } from "./RoutePage";
import { Routes, Route, Link, Navigate } from "react-router-dom";
import { Layout } from "@/app/layouts/CustomerLayout";
import { Protected } from "@/app/router/ProtectedRoute";
import { AdminLayout } from "@/app/layouts/AdminLayout";

const Home = lazy(() =>
  import("@/features/home/pages/HomePage").then((m) => ({ default: m.Home })),
);
const Catalog = lazy(() =>
  import("@/features/catalog/pages/CatalogPage").then((m) => ({
    default: m.Catalog,
  })),
);
const Detail = lazy(() =>
  import("@/features/catalog/pages/ProductDetailPage").then((m) => ({
    default: m.Detail,
  })),
);
const Blog = lazy(() =>
  import("@/features/blog/pages/BlogPage").then((m) => ({ default: m.Blog })),
);
const Post = lazy(() =>
  import("@/features/blog/pages/PostPage").then((m) => ({ default: m.Post })),
);
const Auth = lazy(() =>
  import("@/features/auth/pages/AuthPage").then((m) => ({ default: m.Auth })),
);

const Cart = lazy(() =>
  import("@/features/cart/pages/CartPage").then((m) => ({ default: m.Cart })),
);
const Checkout = lazy(() =>
  import("@/features/checkout/pages/CheckoutPage").then((m) => ({
    default: m.Checkout,
  })),
);
const Orders = lazy(() =>
  import("@/features/orders/pages/OrdersPage").then((m) => ({
    default: m.Orders,
  })),
);
const Favorites = lazy(() =>
  import("@/features/favorites/pages/FavoritesPage").then((m) => ({
    default: m.Favorites,
  })),
);
const Profile = lazy(() =>
  import("@/features/account/pages/ProfilePage").then((m) => ({
    default: m.Profile,
  })),
);
const Vocabulary = lazy(() =>
  import("@/features/vocabulary/pages/VocabularyPage").then((m) => ({
    default: m.Vocabulary,
  })),
);
const MyCourses = lazy(() =>
  import("@/features/learning/pages/MyCoursesPage").then((m) => ({
    default: m.MyCourses,
  })),
);
const Preview = lazy(() =>
  import("@/features/learning/pages/PreviewPage").then((m) => ({
    default: m.Preview,
  })),
);
const Learn = lazy(() =>
  import("@/features/learning/pages/LearnPage").then((m) => ({
    default: m.Learn,
  })),
);
const Practice = lazy(() =>
  import("@/features/exams/pages/PracticePage").then((m) => ({
    default: m.Practice,
  })),
);
const Exam = lazy(() =>
  import("@/features/exams/pages/ExamPage").then((m) => ({ default: m.Exam })),
);
const Result = lazy(() =>
  import("@/features/exams/pages/ResultPage").then((m) => ({
    default: m.Result,
  })),
);

const Admin = lazy(() =>
  import("@/features/admin/pages/AdminPage").then((m) => ({
    default: m.Admin,
  })),
);
export function AppRoutes() {
  return (
    <>
      <Routes>
        <Route element={<Layout />}>
          <Route
            index
            element={
              <RoutePage>
                <Home />
              </RoutePage>
            }
          />
          <Route
            path="books"
            element={
              <RoutePage>
                <Catalog key="books" kind="books" />
              </RoutePage>
            }
          />
          <Route
            path="courses"
            element={
              <RoutePage>
                <Catalog key="courses" kind="courses" />
              </RoutePage>
            }
          />
          <Route
            path="books/:id"
            element={
              <RoutePage>
                <Detail kind="books" />
              </RoutePage>
            }
          />
          <Route
            path="courses/:id"
            element={
              <RoutePage>
                <Detail kind="courses" />
              </RoutePage>
            }
          />
          <Route
            path="blog"
            element={
              <RoutePage>
                <Blog />
              </RoutePage>
            }
          />
          <Route
            path="blog/:id"
            element={
              <RoutePage>
                <Post />
              </RoutePage>
            }
          />
          <Route
            path="login"
            element={
              <RoutePage>
                <Auth key="login" />
              </RoutePage>
            }
          />
          <Route
            path="register"
            element={
              <RoutePage>
                <Auth key="register" mode="register" />
              </RoutePage>
            }
          />
          <Route
            path="forgot-password"
            element={
              <RoutePage>
                <Auth key="forgot" mode="forgot" />
              </RoutePage>
            }
          />
          <Route
            path="reset-password"
            element={
              <RoutePage>
                <Auth key="reset" mode="reset" />
              </RoutePage>
            }
          />
          <Route element={<Protected />}>
            <Route
              path="cart"
              element={
                <RoutePage>
                  <Cart />
                </RoutePage>
              }
            />
            <Route
              path="checkout"
              element={
                <RoutePage>
                  <Checkout />
                </RoutePage>
              }
            />
            <Route
              path="orders"
              element={
                <RoutePage>
                  <Orders />
                </RoutePage>
              }
            />
            <Route
              path="favorites"
              element={
                <RoutePage>
                  <Favorites />
                </RoutePage>
              }
            />
            <Route
              path="profile"
              element={
                <RoutePage>
                  <Profile />
                </RoutePage>
              }
            />
            <Route
              path="vocabulary"
              element={
                <RoutePage>
                  <Vocabulary />
                </RoutePage>
              }
            />
            <Route
              path="my-courses"
              element={
                <RoutePage>
                  <MyCourses />
                </RoutePage>
              }
            />
            <Route
              path="preview/:id"
              element={
                <RoutePage>
                  <Preview />
                </RoutePage>
              }
            />
            <Route
              path="learn/:id"
              element={
                <RoutePage>
                  <Learn />
                </RoutePage>
              }
            />
            <Route
              path="practice"
              element={
                <RoutePage>
                  <Practice />
                </RoutePage>
              }
            />
            <Route
              path="exam/:id"
              element={
                <RoutePage>
                  <Exam />
                </RoutePage>
              }
            />
            <Route
              path="results/:id"
              element={
                <RoutePage>
                  <Result />
                </RoutePage>
              }
            />
          </Route>
          <Route path="home" element={<Navigate to="/" replace />} />
          <Route path="store" element={<Navigate to="/books" replace />} />
          <Route path="course" element={<Navigate to="/courses" replace />} />
          <Route
            path="MyCourse"
            element={<Navigate to="/my-courses" replace />}
          />
          <Route
            path="MyVocabulary"
            element={<Navigate to="/vocabulary" replace />}
          />
          <Route path="MyProduct" element={<Navigate to="/orders" replace />} />
          <Route
            path="MissingPassword"
            element={<Navigate to="/forgot-password" replace />}
          />
          <Route
            path="*"
            element={
              <div className="state">
                <h1>404</h1>
                <p>Trang bạn tìm không tồn tại.</p>
                <Link className="button" to="/">
                  Về trang chủ
                </Link>
              </div>
            }
          />
        </Route>
        <Route element={<Protected admin />}>
          <Route element={<AdminLayout />}>
            <Route
              path="admin"
              element={<Navigate to="/admin/dashboard" replace />}
            />
            <Route
              path="admin/:kind"
              element={
                <RoutePage>
                  <Admin />
                </RoutePage>
              }
            />
          </Route>
        </Route>
      </Routes>
    </>
  );
}
